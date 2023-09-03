# constants
const C1 = "constant1"
const C2 = "constant1"
const C3 = "constant1"

# functions
function f1() string {
    return 'hello world'
}

function f2(string s) string {
    return s
}

# graph
set resource access rights ['read', 'write']

# policy class: pm_admin:policy
create object attribute 'pc1:target' in [POLICY_CLASSES_OA]
create object attribute 'pc2:target' in [POLICY_CLASSES_OA]
create object attribute 'super_policy:target' in [POLICY_CLASSES_OA]

# policy class: super_policy
create policy class 'super_policy'
create user attribute 'super_ua' in ['super_policy']
create user attribute 'super_ua1' in ['super_policy']
associate 'super_ua' and 'super_ua1' with ['*']
associate 'super_ua' and PML_CONSTANTS_TARGET with ['*']
associate 'super_ua' and ADMIN_POLICY_TARGET with ['*']
associate 'super_ua' and PML_FUNCTIONS_TARGET with ['*']
associate 'super_ua' and POLICY_CLASSES_OA with ['*']

# policy class: pc1
create policy class 'pc1'
create user attribute 'ua1' in ['pc1']
set properties of 'ua1' to {'k1': 'v1', 'k': 'v'}
create object attribute 'oa1' in ['pc1']
associate 'ua1' and 'oa1' with ['read', 'create_policy_class', 'write']

# policy class: pc2
create policy class 'pc2'
create user attribute 'ua2' in ['pc2']
create object attribute 'oa2' in ['pc2']
associate 'ua2' and 'oa2' with ['read', 'write']

# users
create user 'super' in ['super_ua', 'super_ua1']
create user 'u1' in ['ua1', 'ua2']
create user 'u2' in ['ua1', 'ua2']

# objects
create object 'o1' in ['oa1', 'oa2']

# prohibitions
create prohibition 'u2-prohibition' deny user 'u2' access rights ['write'] on intersection of ['oa1', 'oa2']

# obligations
create obligation 'o1-obligation' {create rule 'o1-assignment-rule' when any user performs ['assign'] on 'o1' do (evtCtx) {let parent = evtCtx['parent']associate 'ua1' and parent with ['read', 'write']associate 'ua2' and parent with ['read', 'write']}}
