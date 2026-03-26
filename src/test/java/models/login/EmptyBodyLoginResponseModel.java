package models.login;

import java.util.List;

public record EmptyParamsLoginResponseModel(List<String> username, List<String> password) {}