/**
 * Reduce imagen y devuelve data URL JPEG (más liviano para guardar en API / H2).
 */
export async function fileToCompressedDataUrl(file: File, maxWidth = 640): Promise<string> {
  return new Promise((resolve, reject) => {
    const objectUrl = URL.createObjectURL(file)
    const img = new Image()
    img.onload = () => {
      URL.revokeObjectURL(objectUrl)
      let { width, height } = img
      if (width > maxWidth) {
        height = Math.round((height * maxWidth) / width)
        width = maxWidth
      }
      const canvas = document.createElement('canvas')
      canvas.width = width
      canvas.height = height
      const ctx = canvas.getContext('2d')
      if (!ctx) {
        reject(new Error('Canvas no disponible'))
        return
      }
      ctx.drawImage(img, 0, 0, width, height)
      resolve(canvas.toDataURL('image/jpeg', 0.88))
    }
    img.onerror = () => {
      URL.revokeObjectURL(objectUrl)
      reject(new Error('No se pudo procesar la imagen'))
    }
    img.src = objectUrl
  })
}
