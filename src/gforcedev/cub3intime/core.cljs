(ns gforcedev.cub3intime.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [tailwind-hiccup.core :refer [tw]]))

(def dom-root (js/document.getElementById "app"))

(def color-transition [:transition-colors :ease-in-out])
(def short-duration [:duration-300])
(def hover-colors ["hover:text-white" "hover:bg-red-500"])

(defn my-button [button-text]
  [:button.a-non-tw-class
   (tw [:mx-3 :my-4 :font-bold]
       hover-colors
       color-transition short-duration
       {:on-click #(js/alert "surprise!")})
   button-text])

(defn simple-component []
  [:div (tw [:bg-orange-500])
   [:p "I am a component"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]
   (my-button "Test My Button")])

(defn ^:export ^:dev/after-load run []
  (rdom/render [simple-component] dom-root))
