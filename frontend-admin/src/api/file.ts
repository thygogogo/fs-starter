import request from '@/utils/request'

/** 上传文件到 MinIO */
export function uploadFileApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, { code: number; data: string }>('/admin/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
