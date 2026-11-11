import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { LoginResult, User } from '../api/types'
import { setAccessToken } from '../api/client'

interface AuthState {
  token: string | null
  user: User | null
  studentId: number | null
  loginAt: string | null
  setSession: (result: LoginResult) => void
  setStudentId: (id: number | null) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      studentId: null,
      loginAt: null,

      setSession: (result) => {
        setAccessToken(result.token)
        set({
          token: result.token,
          user: result.user,
          studentId: null,
          loginAt: new Date().toISOString(),
        })
      },

      setStudentId: (id) => set({ studentId: id }),

      logout: () => {
        setAccessToken(null)
        set({ token: null, user: null, studentId: null, loginAt: null })
      },
    }),
    {
      name: 'campus-auth',
      partialize: (state) => ({
        token: state.token,
        user: state.user,
        studentId: state.studentId,
        loginAt: state.loginAt,
      }),
      onRehydrateStorage: () => (state) => {
        setAccessToken(state?.token ?? null)
      },
    },
  ),
)
