export type TravelWatchStatus = 'ACTIVE' | 'PAUSED' | 'COMPLETED';

export type TravelWatchRequest = {
  departureLocation: string;
  destination: string;
  startDate: string;
  endDate: string;
  travellers: number;
  flexibilityDays: number;
  maxBudget: number;
  tripType: string;
  preferredHotelRating: number;
};

export type TravelWatch = TravelWatchRequest & {
  id: string;
  userId: string;
  status: TravelWatchStatus;
  createdAt: string;
  updatedAt: string;
};

export type SearchResult = {
  id: string;
  providerName: string;
  destination: string;
  departureAirport: string;
  arrivalAirport: string;
  startDate: string;
  endDate: string;
  flightPrice: number;
  hotelPrice: number;
  packagePrice: number;
  currency: string;
  dealScore: number;
  resultUrl: string;
  searchedAt: string;
};

export type PriceHistoryPoint = {
  id: string;
  providerName: string;
  packagePrice: number;
  flightPrice: number;
  hotelPrice: number;
  currency: string;
  searchedAt: string;
};

export type Recommendation = {
  id: string;
  title: string;
  explanation: string;
  recommendationType: string;
  confidenceScore: number;
  estimatedSaving: number;
  createdAt: string;
};
