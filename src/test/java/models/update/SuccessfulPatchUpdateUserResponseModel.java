package models.update;


public record SuccessfulPatchUpdateUserResponseModel(Integer id, String username, String firstName,
                                                     String lastName, String email, String remoteAddr) {}