package models.registration;

import java.util.List;

public record EmptyParamsRegistrationResponseModel(List<String> username, List<String> password) {}