(ns gforcedev.cub3intime.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [tailwind-hiccup.core :refer [tw]]
            [gforcedev.cub3intime.components.timer :as timer-component]))


(def dom-root (js/document.getElementById "app"))

(defn root-component []
  [:div (tw [:h-screen :w-screen :flex :flex-col])
   (timer-component/timer-component)])

(defn ^:export ^:dev/after-load run []
  (rdom/render [root-component] dom-root))
