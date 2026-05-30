export type TravelWatchStatus = 'ACTIVE' | 'PAUSED' | 'COMPLETED';

export type TravelWatchRequest = {
  departureLocation: string;
  destination: string;
  startDate: string;
  endDate: string;
  range2StartDate: string | null;
  range2EndDate: string | null;
  range3StartDate: string | null;
  range3EndDate: string | null;
  travellers: number;
  flexibilityDays: number;
  startDaysEarly: number;
  startDaysLate: number;
  finishDaysEarly: number;
  finishDaysLate: number;
  durationIncreaseDays: number;
  maxBudget: number;
  tripType: string;
  preferredHotelRating: number;
  travelProductType: string;
  cabinClass: string;
  bucketList: boolean;
  bucketListName: string;
  earliestStartDate: string;
  latestEndDate: string;
  notes: string;
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

export type SearchActivityLog = {
  id: string;
  providerName: string;
  searchType: string;
  departureLocation: string;
  destination: string;
  departureAirport: string;
  arrivalAirport: string;
  startDate: string;
  endDate: string;
  status: string;
  offersReturned: number;
  cheapestPackagePrice: number | null;
  currency: string | null;
  message: string;
  searchedAt: string;
};

export type McpContext = {
  available: boolean;
  context: string;
};
