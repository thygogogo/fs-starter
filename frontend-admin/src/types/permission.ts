export type RoleCode = 'super_admin' | 'admin' | (string & Record<never, never>)

export interface Role {
  code: RoleCode
  name: string
  description: string
}

export interface Permission {
  key: string
  name: string
  type: 'menu' | 'button'
}

export interface RolePermission {
  roleCode: RoleCode
  permissions: string[]
}

export interface Account {
  id: number
  username: string
  nickname: string
  phone: string
  roleCode: RoleCode
  roleName: string
  status: 'active' | 'disabled'
  createTime: string
  lastLoginTime: string
}
