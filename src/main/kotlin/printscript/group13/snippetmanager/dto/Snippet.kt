package printscript.group13.snippetmanager.dto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class Snippet(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "name")
    var name: String,
    @Column(name = "code")
    var code: String,
    @Column(name = "language")
    var language: String,
    @Column(name = "user id")
    val userId: String,
)
