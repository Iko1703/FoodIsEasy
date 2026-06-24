import { useState } from 'react'

type Props = {
  src?: string | null
  alt: string
  className?: string
}

export default function DishImage({ src, alt, className }: Props) {
  const [failed, setFailed] = useState(false)

  if (!src || failed) {
    return (
      <div className={[className, 'card-image-placeholder'].filter(Boolean).join(' ')}>
        🍽️
      </div>
    )
  }

  return (
    <img
      src={src}
      alt={alt}
      className={className}
      loading="lazy"
      onError={() => setFailed(true)}
    />
  )
}
