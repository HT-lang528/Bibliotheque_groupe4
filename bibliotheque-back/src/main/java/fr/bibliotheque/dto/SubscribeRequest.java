package fr.bibliotheque.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;
import java.nio.charset.StandardCharsets;
import jakarta.validation.constraints.Size;

// Aucun champ role : les inscriptions publiques créent uniquement des USER.
public record SubscribeRequest(
    @NotBlank @Size(max = 100) String username,
    @NotBlank @Size(min = 8, max = 72) String password) {
    @AssertTrue(message = "Mot de passe trop long pour le hachage")
    public boolean isPasswordWithinBcryptLimit() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
