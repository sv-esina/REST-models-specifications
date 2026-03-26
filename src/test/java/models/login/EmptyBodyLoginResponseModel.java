package models.login;

import java.util.List;

public record EmptyBodyLoginResponseModel(List<String> username, List<String> password) {}