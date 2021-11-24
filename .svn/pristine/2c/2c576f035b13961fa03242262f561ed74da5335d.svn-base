date = new Date();

userInfo1 = builder.userInfo([accountNonExpired  : true, accountNonLocked: true, address: null, credentialsNonExpired: true,
                              dateLastFailedLogin: null, dateLastLogin: date, datePasswordChanged: null, department: null,
                              emailAddress       : "mstrainovic@emisia.net", emailNotification: true, enabled: true, fax: null, firstName: "Marko",
                              lastName           : "Strainovic", password: "9a08ef76b3ae02adc6e8d52d51ffd7099669073a", phone1: null, phone2: null,
                              timeZone           : "Europe/Belgrade", unsuccessfulAuthCount: 0, userName: "strain", userType: "ORGANIZATION"])
userInfo2 = builder.userInfo([accountNonExpired  : true, accountNonLocked: true, address: null, credentialsNonExpired: true,
                              dateLastFailedLogin: date, dateLastLogin: date, datePasswordChanged: null, department: null,
                              emailAddress       : "something@emisia.net", emailNotification: false, enabled: true, fax: null, firstName: "John",
                              lastName           : "Do", password: "9a08ef76b3ae02adc6e8d52d51ffd7099669073a", phone1: null, phone2: null,
                              timeZone           : "Europe/Belgrade", unsuccessfulAuthCount: 0, userName: "john", userType: "ORGANIZATION"])

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

contextsPolicies = new HashMap<>();
contextsRoles = new HashMap<>();

tmUserProfile1 = builder.tmUserProfile([adminFolders   : null, contextsPolicies: contextsPolicies, contextsRoles: contextsRoles,
                                        folders        : null, generic: false, genericPassword: null, hasChangedTerms: false, hidden: false,
                                        lastDailyReport: null, lastWeeklyReport: null, metadata: null, notificationProfiles: null, organization: tmOrganization,
                                        preferences    : null, projectUserLanguages: null, submissionUserLanguages: null, substituteSystemRoles: null,
                                        systemRoles    : null, userInfo: userInfo1, userProfileId: 1L])

tmUserProfile2 = builder.tmUserProfile([adminFolders   : null, contextsPolicies: contextsPolicies, contextsRoles: contextsRoles,
                                        folders        : null, generic: false, genericPassword: null, hasChangedTerms: false, hidden: false,
                                        lastDailyReport: null, lastWeeklyReport: null, metadata: null, notificationProfiles: null, organization: null,
                                        preferences    : null, projectUserLanguages: null, submissionUserLanguages: null, substituteSystemRoles: null,
                                        systemRoles    : null, userInfo: userInfo2, userProfileId: 2L])
