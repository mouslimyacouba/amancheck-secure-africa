# AmanCheck Secure (Android)

AmanCheck is a native Android security application designed to detect, verify, and combat financial scams and mobile money fraud in Niger and West Africa (Orange Money, Moov Money, Airtel, Wave, etc.).

## Key Features

- **Verification Engine**: Real-time verification of suspicious phone numbers, URLs, and mobile applications with community and algorithmic fraud scoring.
- **AI Security Analyzer**: Screenshot and text analyzer detecting phishing attempts, artificial urgency, OTP theft, and financial impersonation.
- **Community Scam Reporting**: Community-driven reporting of phone numbers, links, and fake apps with photo proof support.
- **Live Alert Feed**: Real-time alerts on active fraud campaigns in West Africa.
- **Bilingual Support**: Full native language switching between French (*Français*) and Hausa (*Hausa*).
- **Security Education Guides**: Practical guides and daily security tips to avoid scams.
- **Local Persistence**: Built with Room database, pre-seeded with regional threat intelligence.

## Technical Architecture

- **Platform**: Android (Kotlin, Jetpack Compose, Material 3)
- **Database**: AndroidX Room with Kotlin Symbol Processing (KSP)
- **Networking & AI**: Gemini API integration with local heuristic security engine fallback
- **Images**: Coil Compose & Android PhotoPicker API
