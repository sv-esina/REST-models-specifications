package models.clubs;


import java.util.List;

public record SuccessfulUpdateClubResponseModel(Integer id, String bookTitle, String bookAuthors, Integer publicationYear,
                                                String description, String telegramChatLink, Integer owner, List<Integer> members,
                                                List<String> reviews, String created, String modified) {}