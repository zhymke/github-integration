package lt.zymantas.githubintegration.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.log4j.Log4j2;
import lt.zymantas.githubintegration.services.GitApiService;

@Log4j2
@RestController
public class GitHubApiController {

    private final GitApiService gitHubApiService;

    public GitHubApiController(GitApiService gitHubApiService) {
        this.gitHubApiService = gitHubApiService;
    }

    @GetMapping("/github/top-frameworks")
    public List<GitQueryResultAnonymous> fetchGitHubReposAnonymous(
            @RequestParam(required = false) String sortByContributors) {
        return gitHubApiService.fetchGitDataAnonymous(sortByContributors);
    }

    @GetMapping(value = "/github/top-frameworks", headers = "Authorization")
    public List<GitQueryResult> fetchGitHubRepos(@RequestParam(required = false) String sortByContributors,
            @RequestHeader String authorization) {
        return gitHubApiService.fetchGitData(authorization, sortByContributors);
    }

    @PostMapping("/github/repository/{id}/star")
    public void starRepository(@PathVariable String id, @RequestHeader String authorization) {
        gitHubApiService.starRepository(authorization, id);
    }

    @DeleteMapping("/github/repository/{id}/star")
    public void unStarRepository(@PathVariable String id, @RequestHeader String authorization) {
        gitHubApiService.unStarRepository(authorization, id);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("Error from WebClient - Status {}, Body {}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
        return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
    }
}