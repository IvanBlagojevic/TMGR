

organizationInfo1 = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization1 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, parentOrganization: null])

organizationInfo2 = builder.organizationInfo([name: "My Child Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization2 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1,
    parentOrganization: tmOrganization1])

allProjectRoleIds = [
    "super_user",
    "translator_user"] as List

organizationInfo = builder.organizationInfo([currencyCode: null, domain: "DOMAIN", enabled: null, name: "Some organization 3",
    theme: "THEME"])

organizationCommand = builder.organizationCommand([name: null, organizationId: null, organizationInfo: organizationInfo,
    parentOrganizationId: null, currencyCode: null])


