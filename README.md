# Cub3intime
This is my third attempt at a cube timer. This time, I'm using:
- [ClojureScript](https://clojurescript.org/)
- [shadow-cljs](https://github.com/thheller/shadow-cljs)
- [reagent](https://github.com/reagent-project/reagent)
- [tailwind-css](https://tailwindcss.com/) (via [tailwind-hiccup](https://github.com/rgm/tailwind-hiccup))

## Setup
This was set up by loosely following an [excellent blog post](https://ghufran.posthaven.com/setting-up-a-reagent-clojurescript-project-with-shadow-cljs-and-cursive) from Standard Deviant. As I'm also using nvim+[conjure](https://github.com/olical/conjure), then I added in the cider middleware. Note: I still don't really know what I'm doing in the clj/cljs world, and so some of this setup is incorrect.

To add tailwind, I followed the guide on [tailwind cli](https://tailwindcss.com/docs/installation) installation, after adding tailwind-hiccup as a dependency in the shadow-cljs config.

## Getting up and running
- `git clone https://github.com/gforcedev/cube3intime.git`
- `cd cub3intime`
- `npm i`
- Then in two separate terminals:
  - `npx shadow-cljs watch :app`
  - `npx tailwindcss -i ./src/input.css -o ./public/main.css --watch`
