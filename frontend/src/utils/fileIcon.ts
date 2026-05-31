export function fileIcon(name: string): string {
  const ext = name.split('.').pop()?.toLowerCase() ?? ''
  const map: Record<string, string> = {
    pdf: '📕', doc: '📘', docx: '📘', xls: '📗', xlsx: '📗', ppt: '📙', pptx: '📙',
    zip: '📦', rar: '📦', '7z': '📦', tar: '📦', gz: '📦',
    jpg: '🖼️', jpeg: '🖼️', png: '🖼️', gif: '🖼️', svg: '🖼️', webp: '🖼️', bmp: '🖼️',
    mp4: '🎬', avi: '🎬', mov: '🎬', mkv: '🎬', webm: '🎬',
    mp3: '🎵', wav: '🎵', flac: '🎵', aac: '🎵', ogg: '🎵',
    js: '📜', ts: '📜', vue: '📜', html: '📜', css: '📜', scss: '📜', jsx: '📜', tsx: '📜',
    md: '📝', txt: '📝', json: '📝', xml: '📝', yml: '📝', yaml: '📝', log: '📝',
    exe: '⚙️', msi: '⚙️', sh: '⚙️', bat: '⚙️', ps1: '⚙️',
    py: '🐍', java: '☕', rs: '🦀', go: '🐹', rb: '💎', c: '⚡', cpp: '⚡',
  }
  return map[ext] ?? '📄'
}
