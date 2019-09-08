package lt.zymantas.githubintegration.services;

import java.util.List;

import lt.zymantas.githubintegration.controllers.GitQueryResult;
import lt.zymantas.githubintegration.controllers.GitQueryResultAnonymous;

public interface GitApiService {

    List<GitQueryResult> fetchGitData(String accessToken, String sortByContributors);

    List<GitQueryResultAnonymous> fetchGitDataAnonymous(String sortByContributors);

    void starRepository(String accessToken, String id);

    void unStarRepository(String accessToken, String id);

    class GitApiServiceException extends RuntimeException {

        private static final long serialVersionUID = -6425280242710365433L;

        protected GitApiServiceException(String msg) {
            super(msg);
        }
    }
}