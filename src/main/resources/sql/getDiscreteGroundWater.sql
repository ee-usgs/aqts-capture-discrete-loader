select
    field_visit_identifier,
	(regexp_match(location_identifier, '(\d*)-*(.*)'))[1] location_identifier,
	coalesce(nullif((regexp_match(location_identifier, '(\d*)-*(.*)'))[2], ''), 'USGS') agency_code,
    start_time,
    end_time,
    party,
    remarks,
    weather,
    is_valid_header_info,
    completed_work,
    last_modified,
    parameter,
    parm_cd,
    monitoring_method,
    nwis_method_code,
    field_visit_value,
    unit,
    uncertainty,
    reading_type,
    manufacturer,
    model,
    serial_number,
    field_visit_time,
    field_visit_comments,
    publish,
    is_valid_readings,
    reference_point_unique_id,
    use_location_datum_as_reference,
    approval_level,
    approval_level_description,
    reading_qualifiers,
    ground_water_measurement,
    datum,
    jsonb_extract_path_text(completed_work, 'CollectionAgency') collection_agency
from
    discrete_ground_water
where
    location_identifier = ?
