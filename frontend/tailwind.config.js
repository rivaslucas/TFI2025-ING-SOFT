/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: {
                    50: '#f0f4ff',
                    100: '#e0e8ff',
                    500: '#667eea',
                    600: '#5a6fd8',
                    700: '#4c5bc0',
                }
            }
        },
    },
    plugins: [],
}