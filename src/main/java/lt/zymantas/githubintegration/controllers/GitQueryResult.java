package lt.zymantas.githubintegration.controllers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitQueryResult extends GitQueryResultAnonymous {

  private boolean viewerHasStarred;

}