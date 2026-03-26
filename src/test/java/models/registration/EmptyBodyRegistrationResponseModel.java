package models.registration;

import java.util.List;

public record EmptyBodyRegistrationResponseModel(List<String> username, List<String> password) {}