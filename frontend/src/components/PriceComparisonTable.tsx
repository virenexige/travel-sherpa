import type { SearchResult } from '../api/types';
import DealScoreBadge from './DealScoreBadge';

export default function PriceComparisonTable({ results }: { results: SearchResult[] }) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Destination</th>
            <th>Route</th>
            <th>Dates</th>
            <th>Flight</th>
            <th>Hotel</th>
            <th>Package</th>
            <th>Score</th>
          </tr>
        </thead>
        <tbody>
          {results.map(result => (
            <tr key={result.id}>
              <td>{result.destination}</td>
              <td>{result.departureAirport} to {result.arrivalAirport}</td>
              <td>{result.startDate} - {result.endDate}</td>
              <td>{result.currency} {result.flightPrice}</td>
              <td>{result.currency} {result.hotelPrice}</td>
              <td><strong>{result.currency} {result.packagePrice}</strong></td>
              <td><DealScoreBadge score={result.dealScore} /></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
