package service;

import domain.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamValidationService {

    public List<String> validateTeam(Team team) {
        List<String> errors = new ArrayList<>();

        if(team.getName() == null){
            errors.add("Team name is required");
        }else if (team.getName().length() < 2 || team.getName().length() > 100) {
            errors.add("Team name must be between 2 and 100 characters");
        }
        return errors;
    }

    public List<String> validateTeamForUpdate(Team team) {
        List<String> errors = validateTeam(team);
        if(team.getTeamId() == null){
            errors.add("Team id is required");
        }
        return errors;
    }

    public List<String> validateTeamForCreate(Team team) {
        List<String> errors = validateTeam(team);
        if(team.getTeamId() != null){
            errors.add("Team ID should not be provided for new teams");
        }
        if(team.getCreatedAt() != null){
            errors.add("Created at should not be provided for new teams");
        }
        return errors;
    }

}
