package models.update;


public record OnlyUsernamePatchUpdateUserResponseModel(Integer id, String username, String firstName,
                                                       String lastName, String email, String remoteAddr) {}