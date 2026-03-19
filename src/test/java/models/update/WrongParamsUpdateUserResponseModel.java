package models.update;


import java.util.List;

public record WrongParamsUpdateUserResponseModel(List<String> firstName, List<String> lastName, List<String> email) {}