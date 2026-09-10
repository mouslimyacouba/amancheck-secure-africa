// Auto-generated from your database schema — do not edit by hand.
// Regenerates automatically whenever a table is created or altered.

export type AlertsRow = {
  id: string
  titleFr: string | null
  titleHa: string | null
  contentFr: string | null
  contentHa: string | null
  type: string | null
  createdAt: string | null
}

export type EducationContentRow = {
  id: string
  titleFr: string | null
  titleHa: string | null
  excerptFr: string | null
  excerptHa: string | null
  contentFr: string | null
  contentHa: string | null
  category: string | null
  createdAt: string | null
}

export type ReportsRow = {
  id: string
  userId: string | null
  type: string | null
  target: string | null
  status: string | null
  description: string | null
  proofUrl: string | null
  createdAt: string | null
}

export type UsersRow = {
  id: string
  email: string
  emailVerified: number | string | null
  displayName: string | null
  avatarUrl: string | null
  phone: string | null
  phoneVerified: number | string | null
  role: string | null
  metadata: string | null
  createdAt: string
  updatedAt: string
  lastSignIn: string
}

export type VerifiedItemsRow = {
  id: string
  type: string | null
  value: string | null
  riskLevel: string | null
  source: string | null
  createdAt: string | null
}
