create extension if not exists pgcrypto;

create table if not exists public.dukas (
    id uuid primary key default gen_random_uuid(),
    owner_id uuid not null references auth.users(id) on delete cascade,
    business_name text not null,
    owner_name text not null,
    phone text not null,
    location text,
    average_daily_sales_cents bigint not null default 0 check (average_daily_sales_cents >= 0),
    currency text not null default 'KES' check (char_length(currency) = 3),
    is_overdue boolean not null default false,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (owner_id),
    unique (phone)
);

create table if not exists public.invoices (
    id uuid primary key default gen_random_uuid(),
    duka_id uuid not null references public.dukas(id) on delete cascade,
    owner_id uuid not null references auth.users(id) on delete cascade,
    supplier_name text not null,
    supplier_till_number text,
    invoice_number text,
    invoice_date date not null,
    total_amount_cents bigint not null check (total_amount_cents >= 0),
    tax_amount_cents bigint check (tax_amount_cents is null or tax_amount_cents >= 0),
    currency text not null default 'KES' check (char_length(currency) = 3),
    status text not null default 'verified' check (status in ('draft', 'verified', 'submitted', 'financed', 'rejected')),
    image_path text,
    extraction_confidence numeric(5,4) not null default 1.0000 check (extraction_confidence >= 0 and extraction_confidence <= 1),
    extraction_flags text[] not null default '{}',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (duka_id, invoice_number)
);

create table if not exists public.invoice_items (
    id uuid primary key default gen_random_uuid(),
    invoice_id uuid not null references public.invoices(id) on delete cascade,
    description text not null,
    quantity numeric(12,3) not null default 1 check (quantity > 0),
    unit_price_cents bigint not null check (unit_price_cents >= 0),
    total_price_cents bigint not null check (total_price_cents >= 0),
    position integer not null default 0 check (position >= 0),
    created_at timestamptz not null default now()
);

create table if not exists public.cashflow_aggregates (
    id uuid primary key default gen_random_uuid(),
    duka_id uuid not null references public.dukas(id) on delete cascade,
    owner_id uuid not null references auth.users(id) on delete cascade,
    aggregate_date date not null,
    gross_sales_cents bigint not null default 0 check (gross_sales_cents >= 0),
    invoice_spend_cents bigint not null default 0 check (invoice_spend_cents >= 0),
    net_cashflow_cents bigint not null default 0,
    transaction_count integer not null default 0 check (transaction_count >= 0),
    source text not null default 'synthetic_seed' check (source in ('pos', 'invoice', 'synthetic_seed')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (duka_id, aggregate_date)
);

create index if not exists dukas_owner_id_idx on public.dukas(owner_id);
create index if not exists invoices_owner_id_idx on public.invoices(owner_id);
create index if not exists invoices_duka_id_invoice_date_idx on public.invoices(duka_id, invoice_date desc);
create index if not exists invoice_items_invoice_id_idx on public.invoice_items(invoice_id);
create index if not exists cashflow_aggregates_owner_date_idx on public.cashflow_aggregates(owner_id, aggregate_date desc);
create index if not exists cashflow_aggregates_duka_date_idx on public.cashflow_aggregates(duka_id, aggregate_date desc);

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

drop trigger if exists set_dukas_updated_at on public.dukas;
create trigger set_dukas_updated_at
before update on public.dukas
for each row execute function public.set_updated_at();

drop trigger if exists set_invoices_updated_at on public.invoices;
create trigger set_invoices_updated_at
before update on public.invoices
for each row execute function public.set_updated_at();

drop trigger if exists set_cashflow_aggregates_updated_at on public.cashflow_aggregates;
create trigger set_cashflow_aggregates_updated_at
before update on public.cashflow_aggregates
for each row execute function public.set_updated_at();

create or replace function public.assert_invoice_owner_matches_duka()
returns trigger
language plpgsql
as $$
begin
    if not exists (
        select 1
        from public.dukas d
        where d.id = new.duka_id
          and d.owner_id::text = new.owner_id::text
    ) then
        raise exception 'invoice owner_id must match duka owner_id';
    end if;

    return new;
end;
$$;

drop trigger if exists assert_invoice_owner_matches_duka on public.invoices;
create trigger assert_invoice_owner_matches_duka
before insert or update of duka_id, owner_id on public.invoices
for each row execute function public.assert_invoice_owner_matches_duka();

create or replace function public.assert_cashflow_owner_matches_duka()
returns trigger
language plpgsql
as $$
begin
    if not exists (
        select 1
        from public.dukas d
        where d.id = new.duka_id
          and d.owner_id::text = new.owner_id::text
    ) then
        raise exception 'cashflow owner_id must match duka owner_id';
    end if;

    return new;
end;
$$;

drop trigger if exists assert_cashflow_owner_matches_duka on public.cashflow_aggregates;
create trigger assert_cashflow_owner_matches_duka
before insert or update of duka_id, owner_id on public.cashflow_aggregates
for each row execute function public.assert_cashflow_owner_matches_duka();

alter table public.dukas enable row level security;
alter table public.invoices enable row level security;
alter table public.invoice_items enable row level security;
alter table public.cashflow_aggregates enable row level security;

drop policy if exists "dukas_select_own" on public.dukas;
create policy "dukas_select_own"
on public.dukas for select
to authenticated
using (owner_id::text = auth.uid()::text);

drop policy if exists "dukas_insert_own" on public.dukas;
create policy "dukas_insert_own"
on public.dukas for insert
to authenticated
with check (owner_id::text = auth.uid()::text);

drop policy if exists "dukas_update_own" on public.dukas;
create policy "dukas_update_own"
on public.dukas for update
to authenticated
using (owner_id::text = auth.uid()::text)
with check (owner_id::text = auth.uid()::text);

drop policy if exists "dukas_delete_own" on public.dukas;
create policy "dukas_delete_own"
on public.dukas for delete
to authenticated
using (owner_id::text = auth.uid()::text);

drop policy if exists "invoices_select_own" on public.invoices;
create policy "invoices_select_own"
on public.invoices for select
to authenticated
using (owner_id::text = auth.uid()::text);

drop policy if exists "invoices_insert_own" on public.invoices;
create policy "invoices_insert_own"
on public.invoices for insert
to authenticated
with check (owner_id::text = auth.uid()::text);

drop policy if exists "invoices_update_own" on public.invoices;
create policy "invoices_update_own"
on public.invoices for update
to authenticated
using (owner_id::text = auth.uid()::text)
with check (owner_id::text = auth.uid()::text);

drop policy if exists "invoices_delete_own" on public.invoices;
create policy "invoices_delete_own"
on public.invoices for delete
to authenticated
using (owner_id::text = auth.uid()::text);

drop policy if exists "invoice_items_select_own" on public.invoice_items;
create policy "invoice_items_select_own"
on public.invoice_items for select
to authenticated
using (
    exists (
        select 1
        from public.invoices i
        where i.id = invoice_items.invoice_id
          and i.owner_id::text = auth.uid()::text
    )
);

drop policy if exists "invoice_items_insert_own" on public.invoice_items;
create policy "invoice_items_insert_own"
on public.invoice_items for insert
to authenticated
with check (
    exists (
        select 1
        from public.invoices i
        where i.id = invoice_items.invoice_id
          and i.owner_id::text = auth.uid()::text
    )
);

drop policy if exists "invoice_items_update_own" on public.invoice_items;
create policy "invoice_items_update_own"
on public.invoice_items for update
to authenticated
using (
    exists (
        select 1
        from public.invoices i
        where i.id = invoice_items.invoice_id
          and i.owner_id::text = auth.uid()::text
    )
)
with check (
    exists (
        select 1
        from public.invoices i
        where i.id = invoice_items.invoice_id
          and i.owner_id::text = auth.uid()::text
    )
);

drop policy if exists "invoice_items_delete_own" on public.invoice_items;
create policy "invoice_items_delete_own"
on public.invoice_items for delete
to authenticated
using (
    exists (
        select 1
        from public.invoices i
        where i.id = invoice_items.invoice_id
          and i.owner_id::text = auth.uid()::text
    )
);

drop policy if exists "cashflow_aggregates_select_own" on public.cashflow_aggregates;
create policy "cashflow_aggregates_select_own"
on public.cashflow_aggregates for select
to authenticated
using (owner_id::text = auth.uid()::text);

drop policy if exists "cashflow_aggregates_insert_own" on public.cashflow_aggregates;
create policy "cashflow_aggregates_insert_own"
on public.cashflow_aggregates for insert
to authenticated
with check (owner_id::text = auth.uid()::text);

drop policy if exists "cashflow_aggregates_update_own" on public.cashflow_aggregates;
create policy "cashflow_aggregates_update_own"
on public.cashflow_aggregates for update
to authenticated
using (owner_id::text = auth.uid()::text)
with check (owner_id::text = auth.uid()::text);

drop policy if exists "cashflow_aggregates_delete_own" on public.cashflow_aggregates;
create policy "cashflow_aggregates_delete_own"
on public.cashflow_aggregates for delete
to authenticated
using (owner_id::text = auth.uid()::text);

insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values (
    'invoice-scans',
    'invoice-scans',
    false,
    10485760,
    array['image/jpeg', 'image/png', 'image/webp']
)
on conflict (id) do update
set public = false,
    file_size_limit = excluded.file_size_limit,
    allowed_mime_types = excluded.allowed_mime_types;

drop policy if exists "invoice_scans_bucket_read" on storage.buckets;
create policy "invoice_scans_bucket_read"
on storage.buckets for select
to authenticated
using (id = 'invoice-scans');

drop policy if exists "invoice_scans_select_own" on storage.objects;
create policy "invoice_scans_select_own"
on storage.objects for select
to authenticated
using (
    bucket_id = 'invoice-scans'
    and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists "invoice_scans_insert_own" on storage.objects;
create policy "invoice_scans_insert_own"
on storage.objects for insert
to authenticated
with check (
    bucket_id = 'invoice-scans'
    and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists "invoice_scans_update_own" on storage.objects;
create policy "invoice_scans_update_own"
on storage.objects for update
to authenticated
using (
    bucket_id = 'invoice-scans'
    and (storage.foldername(name))[1] = auth.uid()::text
)
with check (
    bucket_id = 'invoice-scans'
    and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists "invoice_scans_delete_own" on storage.objects;
create policy "invoice_scans_delete_own"
on storage.objects for delete
to authenticated
using (
    bucket_id = 'invoice-scans'
    and (storage.foldername(name))[1] = auth.uid()::text
);
