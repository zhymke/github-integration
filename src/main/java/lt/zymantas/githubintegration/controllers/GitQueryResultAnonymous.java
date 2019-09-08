package lt.zymantas.githubintegration.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitQueryResultAnonymous{

    @JsonProperty(access = Access.WRITE_ONLY)
    private String nameWithOwner;
    private String name;
    private String description;
    private LicenseInfo licenseInfo;
    private String url;
    private Stargazers stargazers;
    private Integer contributorsCount;

    @Getter
    @Setter
    public static class Stargazers {
        private Integer totalCount;
    }

    
    @Getter
    @Setter
    public static class LicenseInfo {
        private String name;
    }
    
}
