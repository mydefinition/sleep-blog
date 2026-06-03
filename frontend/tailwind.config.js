/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: 'var(--primary)',
      },
      fontFamily: {
        sans: ['"PingFang SC"', '"Microsoft YaHei"', '"Hiragino Sans GB"', '"Noto Sans SC"', '-apple-system', 'BlinkMacSystemFont', 'sans-serif'],
        mono: ['"JetBrains Mono"', '"Consolas"', '"Microsoft YaHei"', 'monospace'],
      },
      animation: {
        spin: 'spin 0.7s linear infinite',
        shake: 'shake 0.6s ease',
        blink: 'blink 1.2s step-end infinite',
        'flash-red': 'flashRed 0.6s ease',
      },
      keyframes: {
        shake: {
          '0%,100%': { transform: 'translateX(0)' },
          '20%': { transform: 'translateX(-8px)' },
          '40%': { transform: 'translateX(8px)' },
          '60%': { transform: 'translateX(-4px)' },
          '80%': { transform: 'translateX(4px)' },
        },
        blink: {
          '0%,100%': { opacity: '1' },
          '50%': { opacity: '0' },
        },
        flashRed: {
          '0%,100%': { color: '#e03131' },
          '50%': { color: '#ff6b6b' },
        },
      },
    },
  },
  plugins: [],
}
