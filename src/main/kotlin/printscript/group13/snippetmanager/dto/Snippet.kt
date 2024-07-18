package printscript.group13.snippetmanager.dto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class Snippet(
    @Id
    @Column(name = "id")
    val id : UUID,
    @Column(name = "name")
    val name: String,
    @Column(name = "code")
    val code: String,
    @Column(name = "language")
    val language: String,
    @Column(name = "user id")
    val userId: UUID
)