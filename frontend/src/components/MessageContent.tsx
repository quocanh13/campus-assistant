import type { ReactNode } from 'react'

interface MessageContentProps {
  text: string
}

const INLINE_PATTERN = /(\*\*[^*]+\*\*|`[^`]+`|\*[^*\n]+\*)/g

function renderInline(text: string, prefix: string): ReactNode[] {
  const nodes: ReactNode[] = []
  let lastIndex = 0
  let tokenIndex = 0
  INLINE_PATTERN.lastIndex = 0

  let match = INLINE_PATTERN.exec(text)
  while (match) {
    if (match.index > lastIndex) {
      nodes.push(text.slice(lastIndex, match.index))
    }
    const token = match[0]
    const key = `${prefix}-${tokenIndex++}`
    if (token.startsWith('**')) {
      nodes.push(<strong key={key}>{token.slice(2, -2)}</strong>)
    } else if (token.startsWith('`')) {
      nodes.push(<code key={key}>{token.slice(1, -1)}</code>)
    } else {
      nodes.push(<em key={key}>{token.slice(1, -1)}</em>)
    }
    lastIndex = match.index + token.length
    match = INLINE_PATTERN.exec(text)
  }

  if (lastIndex < text.length) {
    nodes.push(text.slice(lastIndex))
  }

  return nodes
}

export function MessageContent({ text }: MessageContentProps) {
  const lines = text.replace(/\r\n/g, '\n').split('\n')
  const blocks: ReactNode[] = []
  let paragraph: string[] = []
  let list: { ordered: boolean; items: string[] } | null = null
  let counter = 0

  const pushParagraph = () => {
    if (paragraph.length === 0) return
    const key = `p-${counter++}`
    blocks.push(
      <p key={key}>
        {paragraph.map((line, index) => (
          <span key={`${key}-${index}`}>
            {renderInline(line, `${key}-${index}`)}
            {index < paragraph.length - 1 && <br />}
          </span>
        ))}
      </p>,
    )
    paragraph = []
  }

  const pushList = () => {
    if (!list) return
    const key = `list-${counter++}`
    const items = list.items.map((item, index) => (
      <li key={`${key}-${index}`}>{renderInline(item, `${key}-${index}`)}</li>
    ))
    blocks.push(list.ordered ? <ol key={key}>{items}</ol> : <ul key={key}>{items}</ul>)
    list = null
  }

  lines.forEach((line) => {
    const heading = line.match(/^(#{1,4})\s+(.*)$/)
    const bullet = line.match(/^\s*[*\-•]\s+(.*)$/)
    const ordered = line.match(/^\s*\d+[.)]\s+(.*)$/)

    if (!line.trim()) {
      pushParagraph()
      pushList()
      return
    }

    if (heading) {
      pushParagraph()
      pushList()
      blocks.push(
        <h4 key={`h-${counter++}`} className="message-heading">
          {renderInline(heading[2], `h-${counter}`)}
        </h4>,
      )
      return
    }

    if (bullet) {
      pushParagraph()
      if (!list || list.ordered) {
        pushList()
        list = { ordered: false, items: [] }
      }
      list.items.push(bullet[1])
      return
    }

    if (ordered) {
      pushParagraph()
      if (!list || !list.ordered) {
        pushList()
        list = { ordered: true, items: [] }
      }
      list.items.push(ordered[1])
      return
    }

    pushList()
    paragraph.push(line)
  })

  pushParagraph()
  pushList()

  return <div className="message-text">{blocks}</div>
}
