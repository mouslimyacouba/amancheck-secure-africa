import { createClient } from '@blinkdotnew/sdk'

export const blink = createClient({
  projectId: import.meta.env.VITE_BLINK_PROJECT_ID || 'amancheck-app-7jobegwu',
  publishableKey: import.meta.env.VITE_BLINK_PUBLISHABLE_KEY || 'blnk_pk_zi5C4pXxgdLDJ92k-bV-x8pJDsmmQ5fd',
  authRequired: false,
  auth: { mode: 'managed' },
})
