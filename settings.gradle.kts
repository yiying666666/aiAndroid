pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WanAndroid"
include(":app")
include(":core:model", ":core:network", ":core:data", ":core:ui")
include(":feature:auth", ":feature:home", ":feature:project")
include(":feature:wechat", ":feature:navi", ":feature:mine")
include(":feature:search", ":feature:article")
