package groovy.service
import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;


policy1 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_PROJECT_ADD",
    policyType: "CONTEXT", category: "Project"])
policy2 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_USERPROFILE_VIEW",
    policyType: "CONTEXT", category: "User"])
policy3 = builder.policy([description: "Some polocy description", policyId: "POLICY_FOUNDATION_ORGANIZATION_VIEW",
    policyType: "CONTEXT", category: "Organization"])
policy4 = builder.policy([description: "Some polocy description", policyId: "POLICY_TM_TERM_ADD_APPROVED_TERM",
    policyType: "CONTEXT", category: "Task"])
policy5 = builder.policy([description: "Some polocy description", policyId: "POLICY_TM_TERM_ADD_PENDING_TERM",
    policyType: "CONTEXT", category: "Task"])
policy6 = builder.policy([description: "Some polocy description", policyId: "POLICY_TM_TERMENTRY_IMPORT",
    policyType: "CONTEXT", category: "Task"])

policies1 = [
    policy1,
    policy2,
    policy3,
    policy4,
    policy5,
    policy6
]
policies2 = [
    policy1,
    policy2,
    policy3,
    policy4,
    policy5
]

role1 = builder.role([policies: policies1, roleId: "My Project Role", roleType: "CONTEXT", generic: false])
role2 = builder.role([policies: policies2, roleId: 2, roleType: "SYSTEM", generic: false])


roles = [role1, role2] as List
roles2 = [role2] as List
