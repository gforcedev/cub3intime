(ns gforcedev.cub3intime.components.penalty-button
  (:require [tailwind-hiccup.core :refer [tw]]))

(defn penalty-button-component [current-penalty target-penalty display-string]
  [:button
   (tw [:text-xl :max-w-min :text-center :p-6 :rounded-full "hover:bg-gray-900"]
       {:on-click #(reset! current-penalty target-penalty)})
   display-string])

