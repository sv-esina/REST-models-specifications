package models.login;

import java.util.List;

public record NullParamsLoginResponseModel(List<String> username, List<String> password) {}