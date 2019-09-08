package lt.zymantas.githubintegration.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.log4j.Log4j2;
import lt.zymantas.githubintegration.controllers.GitQueryResult;
import lt.zymantas.githubintegration.controllers.GitQueryResultAnonymous;

@Log4j2
@Service
public class GitHubApiService implements GitApiService {

    private final String URL = "https://api.github.com/";

    private final WebClient webClient;
    private final ObjectMapper mapper;

    public GitHubApiService(WebClient.Builder webClientBuilder, String accessToken, ObjectMapper mapper) {
        this.webClient = webClientBuilder.baseUrl(URL).defaultHeader("Authorization", "Bearer " + accessToken).build();
        this.mapper = mapper;
    }

    @Override
    public List<GitQueryResultAnonymous> fetchGitDataAnonymous(String sortByContributors) {
        List<GitQueryResultAnonymous> result = null;
        JsonNode res;
        try {
            res = webClient.post().uri("/graphql").body(BodyInserters.fromObject(queryPayload().toString())).retrieve()
                    .bodyToMono(JsonNode.class).block();

            result = mapper.readValue(res.get("data").get("search").get("nodes").toString(),
                    new TypeReference<List<GitQueryResultAnonymous>>() {
                    });
        } catch (JSONException | IOException | URISyntaxException e) {
            log.error(e);
            throw new GitApiServiceException("Error while running query");
        }
        result.parallelStream().forEach(r -> r.setContributorsCount(contributors(r.getNameWithOwner())));

        return sortByContributors(result, sortByContributors);
    }

    private JSONObject queryPayload() throws JSONException, IOException, URISyntaxException {
        JSONObject payload = new JSONObject();
        payload.put("query", getGQl("classpath:graphqls/GitHubQuery.gql"));

        JSONObject variables = new JSONObject();
        variables.put("query", "language:java sort:stars-desc topic:framework");

        payload.put("variables", variables);
        return payload;
    }

    private String getGQl(String file) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(ResourceUtils.getFile(file).toURI())), StandardCharsets.UTF_8);
    }

    private Integer contributors(String nameWithOwner) {
        ClientResponse res = webClient.get()
                .uri("/repos/{owner}/{repo}/contributors?per_page=1&anon=1", (Object[]) nameWithOwner.split("/"))
                .exchange().block();

        List<String> links = res.headers().asHttpHeaders().get("Link");
        if (links == null) {
            return 1;
        }

        return parseContributorsFromLinks(links.get(0));
    }

    private Integer parseContributorsFromLinks(String link) {
        Pattern pattern = Pattern.compile("(?<=&page=).*?(?=\\>)");
        Matcher matcher = pattern.matcher(link.split(",")[1].split(";")[0]);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }

    private <T extends GitQueryResultAnonymous> List<T> sortByContributors(List<T> result, String sortByContributors) {
        if ("desc".equals(sortByContributors)) {
            return result.stream().sorted(Comparator.comparingInt(GitQueryResultAnonymous::getContributorsCount))
                    .collect(Collectors.toList());
        }
        if ("asc".equals(sortByContributors)) {
            return result.stream().sorted((a, b) -> b.getContributorsCount().compareTo(a.getContributorsCount()))
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public List<GitQueryResult> fetchGitData(String accessToken, String sortByContributors) {
        WebClient webClient = WebClient.builder().baseUrl(URL).defaultHeader("Authorization", accessToken).build();
        List<GitQueryResult> result = null;
        JsonNode res;
        try {
            res = webClient.post().uri("/graphql").body(BodyInserters.fromObject(queryPayload().toString())).retrieve()
                    .bodyToMono(JsonNode.class).block();

            result = mapper.readValue(res.get("data").get("search").get("nodes").toString(),
                    new TypeReference<List<GitQueryResult>>() {
                    });
        } catch (JSONException | IOException | URISyntaxException e) {
            log.error(e);
            throw new GitApiServiceException("Error while running query");
        }
        result.parallelStream().forEach(r -> r.setContributorsCount(contributors(r.getNameWithOwner())));

        return sortByContributors(result, sortByContributors);
    }

    @Override
    public void starRepository(String accessToken, String id) {
        WebClient webClient = WebClient.builder().baseUrl(URL).defaultHeader("Authorization", accessToken).build();
        try {
            webClient.post().uri("/graphql").body(BodyInserters.fromObject(starPayload(id).toString())).retrieve()
                    .bodyToMono(JsonNode.class).block();
        } catch (JSONException | IOException | URISyntaxException e) {
            log.error(e);
            throw new GitApiServiceException("Error while staring repository");
        }

    }

    private JSONObject starPayload(String id) throws JSONException, IOException, URISyntaxException {
        JSONObject payload = new JSONObject();
        payload.put("query", getGQl("classpath:graphqls/GitHubStarRepository.gql"));
        payload.put("variables", starVariables(id));
        return payload;
    }

    private String starVariables(String id) {
        JSONObject variable = new JSONObject();
        variable.put("repositoryId", id);
        return variable.toString();
    }

    @Override
    public void unStarRepository(String accessToken, String id) {
        WebClient webClient = WebClient.builder().baseUrl(URL).defaultHeader("Authorization", accessToken).build();
        try {
            webClient.post().uri("/graphql").body(BodyInserters.fromObject(unStarPayload(id).toString())).retrieve()
                    .bodyToMono(String.class).block();
        } catch (JSONException | IOException | URISyntaxException e) {
            log.error(e);
            throw new GitApiServiceException("Error while unstaring repository");
        }

    }

    private JSONObject unStarPayload(String id) throws JSONException, IOException, URISyntaxException {
        JSONObject payload = new JSONObject();
        payload.put("query", getGQl("classpath:graphqls/GitHubUnstarRepository.gql"));
        payload.put("variables", starVariables(id));
        return payload;
    }
}