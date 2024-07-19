package printscript.group13.snippetmanager.asset.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.Date
import java.util.UUID

@Entity
data class Snippet(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "name")
    var name: String,
    @Column(name = "language")
    var language: String,
    @Column(name = "extension")
    var extension: String,
    @Column(name = "createdAt")
    var createdAt: Date = Date(),
    @Column(name = "updatedAt")
    var updatedAt: Date? = null,
    @Column(name = "compliance")
    var compliance: ComplianceType = ComplianceType.PENDING,
)
