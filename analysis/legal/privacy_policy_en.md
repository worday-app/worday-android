# Worday — Privacy Policy (Draft)

**Last updated:** 24 November 2026  
**Publication status:** Not ready for publication until the controller identity, contact channel, actual release SDK configuration and retention periods are completed.

## 1. Data controller and contact

Controller: **[Insert legal name and postal address]**.  
Privacy requests: **[Insert privacy contact email]**.

This document must not be published with these placeholders.

## 2. Categories of data that may be processed

Depending on enabled features and user choices:

- **App data:** learning level, daily goal, saved vocabulary, review intervals, quiz results and study sessions. In the reviewed Android implementation, much of this data is stored locally using Room and DataStore.
- **Account data:** if Firebase Authentication is enabled, authentication provider identifiers and account attributes. A complete account and cloud-sync flow was not verified in the reviewed source tree.
- **AI request data:** when a user requests example sentences, the requested word and request parameters may be sent to Gemini. In production, the Gemini credential should remain server-side. This policy must describe the actual backend request and retention behavior.
- **Diagnostics and analytics:** if Firebase Analytics or Crashlytics is enabled, app interactions, app/device version and crash diagnostic information may be processed.
- **Advertising data:** if AdMob is enabled, Google may process advertising identifiers and device/interaction data for ad delivery and measurement. Consent and personalized-ad settings must match the actual configuration.

An SDK dependency in source control does not by itself prove that the SDK collects data. Verify initialization and network traffic in the release build before making definitive disclosures.

## 3. Purposes and legal bases

Data may be processed to provide vocabulary-learning features, store progress, fulfill user-requested AI generation, secure and troubleshoot the service, and—if enabled—provide analytics or advertising.

The controller must identify a valid legal basis for each purpose under applicable law, such as performance of a contract, legal obligation, legitimate interests or consent where required. Optional processing must not be made a condition of core learning functionality without a valid basis.

## 4. Third-party providers and international transfers

- **Firebase / Google:** data may be processed for authentication, analytics or crash reporting if those features are enabled.
- **Gemini API / Google:** the word submitted for an example-sentence request may be processed by Google under the applicable API terms and data settings.
- **AdMob / Google:** if advertising is enabled, data necessary for ad delivery and measurement may be processed.

Data may be transferred outside Türkiye or the EEA. The controller must identify recipients, purposes and a valid transfer mechanism under KVKK Article 9 and/or GDPR Chapter V, as applicable, before publication.

## 5. Retention and security

Local learning data may remain on the device until the user clears app data or uninstalls the app; operating-system backup settings may affect this. Retention of server-side and provider data depends on actual service configuration and contractual terms. **[Insert exact retention periods and deletion procedures before release.]**

Appropriate safeguards should include TLS, access control, least privilege, server-side secret management, log redaction, security updates and backup controls. An API key embedded in a distributed Android package must not be treated as secret.

## 6. User rights

Depending on applicable law, users may request access, correction, deletion, restriction, objection, portability, or withdrawal of consent. Requests may be sent to the contact address above. The controller will handle requests within the time limits and procedures required by applicable law.

## 7. Children

The app's intended audience must be accurately declared in the store listing. If personal data from children is processed, age-appropriate notices, parental authorization and applicable child-protection requirements must be assessed.

## 8. Changes

Updates will be published with a revised date/version. Material changes will be communicated through an appropriate channel.

## 9. Required before publication

1. Controller legal identity, postal address and privacy contact.
2. Actual release-build behavior and data fields for Firebase, Gemini and AdMob.
3. Legal bases, retention periods, recipient/processor list and international-transfer mechanism.
4. Consent management, account/data deletion and child-user assessment.
5. Review by qualified KVKK/GDPR counsel.

*This draft is not legal advice and does not claim that an SDK collects data unless that behavior has been verified.*
