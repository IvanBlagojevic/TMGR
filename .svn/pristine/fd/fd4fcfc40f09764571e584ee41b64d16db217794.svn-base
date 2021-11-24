import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo



organizationInfo1 = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization1 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, parentOrganization: null])

organizationInfo2 = builder.organizationInfo([name: "My Child Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization2 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, 
    parentOrganization: tmOrganization1])