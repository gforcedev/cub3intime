(ns gforcedev.cub3intime.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [tailwind-hiccup.core :refer [tw]]
            ["scrambow" :as scrambow]
            [gforcedev.cub3intime.components.timer :as timer-component]
            [gforcedev.cub3intime.components.scramble :as scramble-component]))

(defonce scrambow-scrambler (new scrambow/Scrambow))
(defn get-3x3-scramble []
  (-> scrambow-scrambler (. setType "333") (. get 1) (get 0) (js->clj) (get "scramble_string")))

(def dom-root (js/document.getElementById "app"))

(defonce app-state (r/atom {:current-time 0
                            :timer-phase :stopped
                            :is-down false
                            :scramble-string (get-3x3-scramble)}))

(defn scramble-callback! []
  (swap! app-state #(assoc % :scramble-string (get-3x3-scramble))))

(defn root-component []
  [:div (tw [:h-screen :w-screen :flex :flex-col])
   [scramble-component/scramble-component (:scramble-string @app-state)]
   [timer-component/timer-component app-state scramble-callback!]])

(defn ^:export ^:dev/after-load run []
  (rdom/render [root-component] dom-root))
