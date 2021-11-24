package groovy.webservice

languages1 = ["en-US", "fr-FR", "de-DE"] as List
languages2 = ["en-US", "fr-FR", "no-NO"] as List

projectMetadata1 = builder.projectMetadata([languages: languages1, organizationName: "Organization1", projectName: "testProject1", projectShortcode: "TEST001", username: "genericUser", password: "password1!"])

projectMetadata2 = builder.projectMetadata([languages: languages2, organizationName: "Organization2", projectName: "testProject2", projectShortcode: "TEST002", username: "generic", password: "genericPassword1!"])

projectMetadataList = [projectMetadata1, projectMetadata2] as List

commandlanguages = ["en-US", "fr-FR"] as List

projectMetadataCommand = builder.getProjectMetadataCommand([languages: commandlanguages, organizationName: "organization", projectName: "test", username: "generic"])

invalidProjectMetadataCommand = builder.getProjectMetadataCommand([projectShortcode: "TEST003"])
