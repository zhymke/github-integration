package lt.zymantas.githubintegration;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import lt.zymantas.githubintegration.controllers.GitQueryResultAnonymous;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GitHubApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Value("${github.user.token.test}")
    private String userToken;

    @Test
    public void Should_FetchData_When_GitHubApiWithNoCredentialsIsCalled() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/github/top-frameworks"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].url", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].stargazers", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].contributorsCount", Matchers.notNullValue()));
    }

    @Test
    public void Should_FetchDataAndSortDesc_When_GitHubApiWithNoCredentialsIsCalled() throws Exception {
        ResultActions resultActions = mvc
                .perform(MockMvcRequestBuilders.get("/github/top-frameworks?sortByContributors=desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(10)));

        List<GitQueryResultAnonymous> actual = mapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<GitQueryResultAnonymous>>() {
                });

        List<GitQueryResultAnonymous> expected = actual.stream()
                .sorted(Comparator.comparingInt(GitQueryResultAnonymous::getContributorsCount))
                .collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    @Test
    public void Should_FetchDataAndSortAsc_When_GitHubApiWithNoCredentialsIsCalled() throws Exception {
        ResultActions resultActions = mvc
                .perform(MockMvcRequestBuilders.get("/github/top-frameworks?sortByContributors=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(10)));

        List<GitQueryResultAnonymous> actual = mapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<GitQueryResultAnonymous>>() {
                });

        List<GitQueryResultAnonymous> expected = actual.stream()
                .sorted((a, b) -> b.getContributorsCount().compareTo(a.getContributorsCount()))
                .collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    @Test
    public void Should_FetchData_When_GitHubApiWithCredentialsIsCalledHasFieldViewerHasStarred() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/github/top-frameworks").header("Authorization", userToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].viewerHasStarred", Matchers.notNullValue()));
    }

    @Test
    public void Should_ReturnUnAuthorized_When_StaringARepoWithWrongToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/github/repository/{id}/star", "MDEwOlJlcG9zaXRvcnk2Mjk2Nzkw")
                .header("Authorization", "some value")).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void Should_ReturnUnAuthorized_When_UnStaringARepoWithWrongToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/github/repository/{id}/star", "MDEwOlJlcG9zaXRvcnk2Mjk2Nzkw")
                .header("Authorization", "some value")).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void Should_ReturnBadRequest_When_StaringARepoWithMissingAuthorizationHeader() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/github/repository/{id}/star", "MDEwOlJlcG9zaXRvcnk2Mjk2Nzkw"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void Should_ReturnBadRequest_When_UnStaringARepoWithMissingAuthorizationHeader() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/github/repository/{id}/star", "MDEwOlJlcG9zaXRvcnk2Mjk2Nzkw"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void Should_ReturnIsOk_When_StaringARepoWithAuthorizationHeader() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/github/repository/{id}/star", "MDEwOlJlcG9zaXRvcnk2Mjk2Nzkw")
                .header("Authorization", userToken)).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void Should_ReturnIsOk_When_UnStaringARepoWithAuthorizationHeader() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/github/repository/{id}/star", "MDEwOlJlcG9zaXRvcnk2Mjk2Nzkw")
                .header("Authorization", userToken)).andExpect(MockMvcResultMatchers.status().isOk());
    }
}