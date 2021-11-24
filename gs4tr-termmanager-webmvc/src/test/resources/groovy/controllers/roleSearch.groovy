import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo


policy1 = builder.policy([description: "Some polocy description", policyId: "POLICY_TM_PROJECT_SEND_CONNECTION", 
    policyType: "CONTEXT", category: "Project"])
policy2 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_PROJECT_ADD",
    policyType: "CONTEXT", category: "Project"])
policy3 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_SECURITY_ROLE_VIEW",
    policyType: "CONTEXT", category: "Role"])
policy4 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_SECURITY_ROLE_ADD",
    policyType: "CONTEXT", category: "Role"])
policy5 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_USERPROFILE_ENABLEDISABLE",
    policyType: "CONTEXT", category: "User"])
policy6 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_USERPROFILE_VIEW",
    policyType: "CONTEXT", category: "User"])
policy7 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_PROJECT_ENABLEDISABLE",
    policyType: "CONTEXT", category: "Project"])
policy8 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_ORGANIZATION_VIEW",
    policyType: "CONTEXT", category: "Organization"])
policy9 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_USERPROFILE_EDIT",
    policyType: "CONTEXT", category: "User"])
policy10 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_USERPROFILE_ADD",
    policyType: "CONTEXT", category: "User"])
policy11 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_ORGANIZATION_EDIT",
    policyType: "CONTEXT", category: "Organization"])
policy12 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_ORGANIZATION_ENABLEDISABLE",
    policyType: "CONTEXT", category: "Organization"])
policy13 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_ORGANIZATION_ADD",
    policyType: "CONTEXT", category: "Organization"])

policies1 = [policy1, policy2, policy3, policy4, policy5, policy6, policy7, policy8, policy9, policy11, policy11, policy12, policy13]
policies2 = [policy1, policy2, policy3, policy4, policy5]
policies3 = [policy9, policy11, policy11, policy12, policy13]


role1 = builder.role([policies: policies1, roleId: 1, roleType: "CONTEXT", generic: false])
role2 = builder.role([policies: policies2, roleId: 2, roleType: "SYSTEM", generic: false])
role3 = builder.role([policies: policies3, roleId: 3, roleType: "SYSTEM", generic: true])

