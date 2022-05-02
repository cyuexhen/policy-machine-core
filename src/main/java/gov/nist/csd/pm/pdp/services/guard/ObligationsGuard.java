package gov.nist.csd.pm.pdp.services.guard;

import gov.nist.csd.pm.exceptions.PMAuthorizationException;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pdp.decider.Decider;
import gov.nist.csd.pm.pdp.services.UserContext;
import gov.nist.csd.pm.common.PolicyStore;

import static gov.nist.csd.pm.operations.Operations.*;
import static gov.nist.csd.pm.pap.policies.SuperPolicy.SUPER_OA;

public class ObligationsGuard extends Guard {

    public ObligationsGuard(PolicyStore pap, Decider decider) {
        super(pap, decider);
    }

    public void checkAdd(UserContext userCtx) throws PMException {
        // check that the user can create a policy class
        if (!hasPermissions(userCtx, SUPER_OA, ADD_OBLIGATION)) {
            throw new PMAuthorizationException("unauthorized permissions to add an obligation");
        }
    }

    public void checkGet(UserContext userCtx) throws PMException {
        // check that the user can create a policy class
        if (!hasPermissions(userCtx, SUPER_OA, GET_OBLIGATION)) {
            throw new PMAuthorizationException("unauthorized permissions to get obligation");
        }
    }

    public void checkUpdate(UserContext userCtx) throws PMException {
        // check that the user can create a policy class
        if (!hasPermissions(userCtx, SUPER_OA, UPDATE_OBLIGATION)) {
            throw new PMAuthorizationException("unauthorized permissions to update obligation");
        }
    }

    public void checkDelete(UserContext userCtx) throws PMException {
        // check that the user can create a policy class
        if (!hasPermissions(userCtx, SUPER_OA, DELETE_OBLIGATION)) {
            throw new PMAuthorizationException("unauthorized permissions to delete obligation");
        }
    }

    public void checkEnable(UserContext userCtx) throws PMException {
        // check that the user can create a policy class
        if (!hasPermissions(userCtx, SUPER_OA, ENABLE_OBLIGATION)) {
            throw new PMAuthorizationException("unauthorized permissions to enable obligation");
        }
    }
}
