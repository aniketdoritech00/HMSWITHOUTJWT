package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.VendorMasterEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.VendorMasterRepository;


/**
 * Resolves email recipients based on escalation recipient type.
 *
 * Recipient types:
 *   1. VENDOR: vendor email (via system_integrator)
 *   2. ZONE: zone office 6 emails (with hierarchy traversal)
 *   3. HEAD_OFFICE: head office 6 emails (with hierarchy traversal)
 */
@Service
public class EmailRecipientResolver {

    private static final Logger log = LoggerFactory.getLogger(EmailRecipientResolver.class);

    private final BranchMasterRepository branchMasterRepository;
    private final VendorMasterRepository vendorMasterRepository;
    private final DeviceZoneDetailsRepository deviceZoneDetailsRepository;
    public EmailRecipientResolver(BranchMasterRepository branchMasterRepository,
			VendorMasterRepository vendorMasterRepository,DeviceZoneDetailsRepository deviceZoneDetailsRepository) {
		super();
		this.branchMasterRepository = branchMasterRepository;
		this.vendorMasterRepository = vendorMasterRepository;
		this.deviceZoneDetailsRepository = deviceZoneDetailsRepository;
    }

	/**
     * Resolve recipient emails based on recipient type.
     *
     * @param recipientType "VENDOR", "ZONE", "HEAD_OFFICE"
     * @param branchCode current branch code
     * @return comma-separated email list (e.g., "a@x.com,b@x.com")
     */
    public String resolveRecipients(String recipientType, String branchCode,String vendors) {
        log.info("[EMAIL_RESOLVER] Type='{}', branchCode='{}'", recipientType, branchCode);
        VendorMasterEntity vendorMaster = vendorMasterRepository.findByVendorName(vendors).get();
        try {
            if ("VENDOR".equalsIgnoreCase(recipientType)) {
                return vendorMaster.getEmail();
            } else if ("ZONE".equalsIgnoreCase(recipientType)) {
                return resolveZoneEmails(branchCode);
            } else if ("HEAD_OFFICE".equalsIgnoreCase(recipientType) || 
                       "HEAD OFFICE".equalsIgnoreCase(recipientType)) {
                return resolveHeadOfficeEmails(branchCode);
            } else if ("BRANCH".equalsIgnoreCase(recipientType)) {
                return resolveBranchEmails(branchCode);
            }else {
                log.warn("[EMAIL_RESOLVER] Unknown recipient type: '{}'", recipientType);
                return null;
            }
        } catch (Exception e) {
            log.error("[EMAIL_RESOLVER] Error resolving recipients: {}", e.getMessage(), e);
            return null;
        }
    }

    
    private String resolveBranchEmails(String branchCode) {
        try {
            BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchCode);
            if (branch == null) {
                log.warn("[BRANCH] Branch '{}' not found", branchCode);
                return null;
            }

            String branchType = branch.getBranchType();
            log.info("[ BRANCH] Branch type: '{}'", branchType);

            // Case 1: Already SUPER HO / HEAD OFFICE / ZONE → use its 6 emails
            if (isSuperHoOrHeadOfficeOrZone(branchType)) {
                return collect6Emails(branch);
            }

            // Case 2: BRANCH → traverse to parent (ZONE)
            if ("BRANCH".equalsIgnoreCase(branchType)) {
                
                BranchMasterEntity branchs = branchMasterRepository.findBybranchCode(branch.getBranchCode());
                if (branchs == null) {
                    log.warn("[BRANCH] Parent '{}' not found", branchs);
                    return null;
                }

                log.info("[BRANCH] BRANCH → ZONE: '{}'", branchs);
                return collect6Emails(branchs);
            }

            log.warn("[BRANCH] Unknown branch type: '{}'", branchType);
            return null;

        } catch (Exception e) {
            log.error("[BRANCH] Error: {}", e.getMessage(), e);
            return null;
        }
    }
    
    // =========================================================================
    //  VENDOR — system_integrator → vendor_master.email
    // =========================================================================
    private String resolveVendorEmail(String branchCode) {
        try {
            BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchCode);
            if (branch == null) {
                log.warn("[VENDOR] Branch '{}' not found", branchCode);
                return null;
            }

            String vendorId = branch.getSystemIntegrator();
            if (vendorId == null || vendorId.isBlank()) {
                log.warn("[VENDOR] No system_integrator for branch '{}'", branchCode);
                return null;
            }

            VendorMasterEntity vendor = vendorMasterRepository.findByVendorId(vendorId);
            if (vendor == null) {
                log.warn("[VENDOR] Vendor '{}' not found", vendorId);
                return null;
            }

            String email = vendor.getEmail();
            if (email == null || email.isBlank()) {
                log.warn("[VENDOR] Vendor '{}' has no email", vendorId);
                return null;
            }

            log.info("[VENDOR] Resolved: vendor='{}', email='{}'", vendorId, email);
            return email;

        } catch (Exception e) {
            log.error("[VENDOR] Error: {}", e.getMessage(), e);
            return null;
        }
    }

// =========================================================================
//  ZONE — 6 emails from zone office
// =========================================================================
private String resolveZoneEmails(String branchCode) {
    try {
        BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchCode);
        if (branch == null) {
            log.warn("[ZONE] Branch '{}' not found", branchCode);
            return null;
        }

        String branchType = branch.getBranchType();
        log.info("[ZONE] Branch type: '{}'", branchType);

        // Case 1: Already SUPER HO / HEAD OFFICE / ZONE → use its 6 emails
        if (isSuperHoOrHeadOfficeOrZone(branchType)) {
            return collect6Emails(branch);
        }

        // Case 2: BRANCH → traverse to parent (ZONE)
        if ("BRANCH".equalsIgnoreCase(branchType)) {
            String parentCode = branch.getParentCode();
            if (parentCode == null || parentCode.isBlank()) {
                log.warn("[ZONE] BRANCH '{}' has no parent", branchCode);
                return null;
            }

            BranchMasterEntity parentBranch = branchMasterRepository.findBybranchCode(parentCode);
            if (parentBranch == null) {
                log.warn("[ZONE] Parent '{}' not found", parentCode);
                return null;
            }

            if ("ZONE".equalsIgnoreCase(parentBranch.getBranchType())) {
                log.info("[ZONE] BRANCH → ZONE: '{}'", parentCode);
                return collect6Emails(parentBranch);
            } else {
                log.info("[ZONE] BRANCH → PARENT BRANCH TYPE NOT ZONE WHERE BRANCH CODE: '{}', BRANCH TYPE: '{}'",
                        parentCode, parentBranch.getBranchType());
                return null;
            }
        }

        log.warn("[ZONE] Unknown branch type: '{}'", branchType);
        return null;

    } catch (Exception e) {
        log.error("[ZONE] Error: {}", e.getMessage(), e);
        return null;
    }
}

    // =========================================================================
    //  HEAD_OFFICE — 6 emails from head office (with traversal)
    // =========================================================================
    private String resolveHeadOfficeEmails(String branchCode) {
        try {
            BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchCode);
            if (branch == null) {
                log.warn("[HO] Branch '{}' not found", branchCode);
                return null;
            }

            String branchType = branch.getBranchType();
            log.info("[HO] Branch type: '{}'", branchType);

            // Case 1: Already SUPER HO / HEAD OFFICE → use its 6 emails
            if (isSuperHoOrHeadOffice(branchType)) {
                return collect6Emails(branch);
            }

            // Case 2: ZONE → traverse to parent (HEAD OFFICE)
            if ("ZONE".equalsIgnoreCase(branchType)) {
                String parentCode = branch.getParentCode();
                if (parentCode == null || parentCode.isBlank()) {
                    log.warn("[HO] ZONE '{}' has no parent", branchCode);
                    return null;
                }

                BranchMasterEntity parentBranch = branchMasterRepository.findBybranchCode(parentCode);
                if (parentBranch == null) {
                    log.warn("[HO] Parent '{}' not found", parentCode);
                    return null;
                }

                log.info("[HO] ZONE → HEAD OFFICE: '{}'", parentCode);
                return collect6Emails(parentBranch);
            }

            // Case 3: BRANCH → ZONE → HEAD OFFICE (double traversal)
            if ("BRANCH".equalsIgnoreCase(branchType)) {
                // Step 1: BRANCH → ZONE
                String parentCode = branch.getParentCode();
                if (parentCode == null || parentCode.isBlank()) {
                    log.warn("[HO] BRANCH '{}' has no parent", branchCode);
                    return null;
                }

                BranchMasterEntity zoneBranch = branchMasterRepository.findBybranchCode(parentCode);
                if (zoneBranch == null) {
                    log.warn("[HO] Zone '{}' not found", parentCode);
                    return null;
                }

                log.info("[HO] BRANCH → ZONE: '{}'", parentCode);

                // Step 2: Check if ZONE → if yes, get its parent (HEAD OFFICE)
                if (!"ZONE".equalsIgnoreCase(zoneBranch.getBranchType())) {
                    log.warn("[HO] Parent '{}' is not ZONE (type='{}')", parentCode, zoneBranch.getBranchType());
                    if ("HEAD OFFICE".equalsIgnoreCase(zoneBranch.getBranchType())) {
                    
                    	return collect6Emails(zoneBranch);
                    }
                }

                String hoCode = zoneBranch.getParentCode();
                if (hoCode == null || hoCode.isBlank()) {
                    log.warn("[HO] ZONE '{}' has no parent", parentCode);
                    return null;
                }

                BranchMasterEntity hoBranch = branchMasterRepository.findBybranchCode(hoCode);
                if (hoBranch == null) {
                    log.warn("[HO] HEAD OFFICE '{}' not found", hoCode);
                    return null;
                }

                log.info("[HO] ZONE → HEAD OFFICE: '{}'", hoCode);

                // Verify it's actually HEAD OFFICE
                if (!isSuperHoOrHeadOffice(hoBranch.getBranchType())) {
                    log.warn("[HO] Branch '{}' is not HEAD OFFICE (type='{}')", hoCode, hoBranch.getBranchType());
                    return null;
                }

                return collect6Emails(hoBranch);
            }

            log.warn("[HO] Unknown branch type: '{}'", branchType);
            return null;

        } catch (Exception e) {
            log.error("[HO] Error: {}", e.getMessage(), e);
            return null;
        }
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private boolean isSuperHoOrHeadOffice(String branchType) {
        if (branchType == null) return false;
        return branchType.toUpperCase().contains("SUPER HO") ||
               branchType.toUpperCase().contains("HEAD OFFICE");
    }

    private boolean isSuperHoOrHeadOfficeOrZone(String branchType) {
        if (branchType == null) return false;
        String upper = branchType.toUpperCase();
        return upper.contains("SUPER HO") ||
               upper.contains("HEAD OFFICE") ||
               upper.equals("ZONE");
    }

    /**
     * Collect all 6 emails from a branch, filter out nulls/blanks, join with comma.
     */
    private String collect6Emails(BranchMasterEntity branch) {
        List<String> emails = Arrays.asList(
            branch.getEmail(),
            branch.getEmail2(),
            branch.getEmail3(),
            branch.getEmail4(),
            branch.getEmail5(),
            branch.getEmail6()
        );

        String result = emails.stream()
            .filter(e -> e != null && !e.isBlank())
            .collect(Collectors.joining(","));

        log.info("[COLLECT] Branch '{}' emails: {}", branch.getBranchCode(), result);
        return result.isBlank() ? null : result;
    }

	
}