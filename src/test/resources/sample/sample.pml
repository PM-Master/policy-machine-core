// pml functions and constants
const TEST_CONST = "hello world"
function testFunc(string name) {
    create pc name
}

// resource operations
set resource access rights ["read", "write"]

// GRAPH
// policy classes
create PC "pc1"
create PC "pc2"
testFunc(TEST_CONST)

// user attributes
create UA "ua1" assign to ["pc1"]
create UA "ua2" assign to ["pc1", "pc2"]
create UA "ua3" assign to ["ua2"]

// object attributes
create OA "oa1" assign to ["pc1"]
create OA "oa2" assign to ["pc1", "pc2", pcTargetName(TEST_CONST)]

associate "ua1" and "oa1" with ["write"]
associate "ua2" and "oa2" with ["read"]

associate "ua1" and POLICY_CLASS_TARGETS with ["*a"]
associate "ua1" and PML_FUNCTIONS_TARGET with ["*a"]
associate "ua1" and pcTargetName(TEST_CONST) with ["*a"]

// users
create U "u1" assign to ["ua1", "ua2"]
create U "u2" assign to ["ua2"]

// objects
create O "o1" assign to ["oa1", "oa2"]

// PROHIBITIONS
create prohibition "p1" deny user "u1" access rights ["write"] on intersection of ["oa1", "oa2"]
create prohibition "p2" deny user "u1" access rights ["write"] on intersection of ["oa1", "oa2"]

// OBLIGATIONS
create obligation "o1" {
    create rule "o1-assignment-rule"
    when subject => pAny()
    performs op => pEquals("assign")
    on operand1 => pEquals("o1")
    do (evtCtx) {
        parent := evtCtx["parent"]
        associate "ua1" and parent with ["read", "write"]
        associate "ua2" and parent with ["read", "write"]
        create prohibition "u2-prohibition" deny user "u2" access rights ["write"] on intersection of ["oa1", "oa2"]
    }
}
create obligation "o2" {
    create rule "o1-assignment-rule"
    when subject => pAny()
    performs op => pEquals("assign")
    on operand1 => pEquals("o1")
    do (evtCtx) {
        parent := evtCtx["parent"]
        associate "ua1" and parent with ["read", "write"]
        associate "ua2" and parent with ["read", "write"]
        create prohibition "u2-prohibition" deny user "u2" access rights ["write"] on intersection of ["oa1", "oa2"]
    }
}