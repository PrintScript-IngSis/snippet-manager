package printscript.group13.snippetmanager.asset.model

enum class ComplianceType(val value: String) {
    PENDING("pending"),
    FAILED("failed"),
    NOT_COMPLIANT("not-compliant"),
    COMPLIANT("compliant"),
    ;

    fun toValue(): String {
        return this.value
    }
}
