package com.exadel.frs.controller;

import static com.exadel.frs.system.global.Constants.GUID_EXAMPLE;
import com.exadel.frs.dto.ui.OrgCreateDto;
import com.exadel.frs.dto.ui.OrgResponseDto;
import com.exadel.frs.dto.ui.OrgUpdateDto;
import com.exadel.frs.dto.ui.UserRoleResponseDto;
import com.exadel.frs.dto.ui.UserRoleUpdateDto;
import com.exadel.frs.enums.OrganizationRole;
import com.exadel.frs.helpers.SecurityUtils;
import com.exadel.frs.mapper.OrganizationMapper;
import com.exadel.frs.mapper.UserOrgRoleMapper;
import com.exadel.frs.service.OrganizationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;
    private final UserOrgRoleMapper userOrgRoleMapper;

    @GetMapping("/org/{guid}")
    @ApiOperation(value = "Get organization")
    public OrgResponseDto getOrganization(
            @ApiParam(value = "GUID of organization to return", required = true, example = GUID_EXAMPLE)
            @PathVariable
            final String guid
    ) {
        return organizationMapper.toResponseDto(
                organizationService.getOrganization(guid, SecurityUtils.getPrincipalId()),
                SecurityUtils.getPrincipalId()
        );
    }

    @GetMapping("/orgs")
    @ApiOperation(value = "Get all organizations, the user is a member of")
    public List<OrgResponseDto> getOrganizations() {
        return organizationMapper.toResponseDto(
                organizationService.getOrganizations(SecurityUtils.getPrincipalId()),
                SecurityUtils.getPrincipalId()
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/org")
    @ApiOperation(value = "Create organization")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Organization name is required")
    })
    public OrgResponseDto createOrganization(
            @ApiParam(value = "Organization object that needs to be created", required = true)
            @Valid
            @RequestBody
            final OrgCreateDto orgCreateDto
    ) {
        return organizationMapper.toResponseDto(
                organizationService.createOrganization(orgCreateDto, SecurityUtils.getPrincipalId()),
                SecurityUtils.getPrincipalId()
        );
    }

    @PutMapping("/org/{guid}")
    @ApiOperation(value = "Update organization name")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Organization name is required")
    })
    public OrgResponseDto updateOrganization(
            @ApiParam(value = "GUID of organization that needs to be updated", required = true, example = GUID_EXAMPLE)
            @PathVariable
            final String guid,
            @ApiParam(value = "Organization data", required = true)
            @Valid
            @RequestBody
            final OrgUpdateDto orgUpdateDto
    ) {
        var updatedOrganization = organizationService.updateOrganization(orgUpdateDto, guid, SecurityUtils.getPrincipalId());

        return organizationMapper.toResponseDto(updatedOrganization, SecurityUtils.getPrincipalId());
    }

    @DeleteMapping("/org/{guid}")
    @ApiOperation(value = "Delete organization")
    public void deleteOrganization(
            @ApiParam(value = "GUID of the organization that needs to be deleted", required = true, example = GUID_EXAMPLE)
            @PathVariable
            final String guid
    ) {
        organizationService.deleteOrganization(guid, SecurityUtils.getPrincipalId());
    }

    @GetMapping("/org/roles")
    @ApiOperation(value = "Get all user roles for organization")
    public OrganizationRole[] getOrgRoles() {
        return OrganizationRole.values();
    }

    @GetMapping("/org/{guid}/assign-roles")
    @ApiOperation(value = "Get organization roles, that can be assigned to other users")
    public OrganizationRole[] getOrgRolesToAssign(
            @ApiParam(value = "GUID of the organization", required = true, example = GUID_EXAMPLE)
            @PathVariable
            final String guid
    ) {
        return organizationService.getOrgRolesToAssign(guid, SecurityUtils.getPrincipalId());
    }

    @GetMapping("/org/{guid}/roles")
    @ApiOperation(value = "Get all users of organization")
    public List<UserRoleResponseDto> getOrgUsers(
            @ApiParam(value = "GUID of organization", required = true, example = GUID_EXAMPLE)
            @PathVariable
            final String guid
    ) {
        return userOrgRoleMapper.toUserRoleResponseDto(
                organizationService.getOrgUsers(guid, SecurityUtils.getPrincipalId())
        );
    }

    @PutMapping("/org/{guid}/role")
    @ApiOperation(value = "Update user organization role")
    public UserRoleResponseDto updateUserOrgRole(
            @ApiParam(value = "GUID of organization", required = true, example = GUID_EXAMPLE)
            @PathVariable
            final String guid,
            @ApiParam(value = "User role data", required = true)
            @Valid
            @RequestBody
            final UserRoleUpdateDto userRoleUpdateDto
    ) {
        final Long admin = SecurityUtils.getPrincipalId();
        val updatedUserOrgRole = organizationService.updateUserOrgRole(userRoleUpdateDto, guid, admin);

        return userOrgRoleMapper.toUserRoleResponseDto(updatedUserOrgRole);
    }
}