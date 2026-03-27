package models.clubs;


import java.util.List;

public record EmptyBodyCreateClubResponseModel(List<String> bookTitle, List<String> bookAuthors,List<String> publicationYear,
                                               List<String> description, List<String> telegramChatLink) {}