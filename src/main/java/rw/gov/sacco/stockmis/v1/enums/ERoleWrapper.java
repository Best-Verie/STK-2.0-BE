package rw.gov.sacco.stockmis.v1.enums;

import java.util.ArrayList;
import java.util.List;

public class ERoleWrapper {
    private List<ERole> allowedRoles = new ArrayList<ERole>();

    public ERoleWrapper() {
        allowedRoles.add(ERole.STORE_KEEPER);
        allowedRoles.add(ERole.INITIATOR);
        allowedRoles.add(ERole.APPROVER);
        allowedRoles.add(ERole.PO_REQUEST);
        allowedRoles.add(ERole.PO_APPROVER);
        allowedRoles.add(ERole.HO_HR);
        allowedRoles.add(ERole.DIRECTOR_GENERAL);
    }

    public ERole[] getAllowedRoles() {
        return allowedRoles.toArray(new ERole[0]);
    }
}
