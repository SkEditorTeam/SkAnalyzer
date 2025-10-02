plugins {
    id("skanalyzer.common-conventions")
    id("net.kyori.indra.publishing")
}

indra {
    publishReleasesTo("roxymc", "https://repo.roxymc.net/releases")
    publishSnapshotsTo("roxymc", "https://repo.roxymc.net/snapshots")

    includeJavaSoftwareComponentInPublications(true)
}
